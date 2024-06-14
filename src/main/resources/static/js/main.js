// bat che do nghiem ngat cho tỉnh bien dich, tao ra nhieu thong bao loi huu ich hon -->de bao tri
'use strict';

const usernamePage = document.querySelector('#username-page');//tuong tu nhu getElementById
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

const fileForm = document.querySelector("#fileForm")

let stompClient = null;
let emailResponse = null;
let passwordResponse = null;
let selectedUserId = null;
let token = null
async function connect(event) {
    event.preventDefault();
    console.log("connect");
    emailResponse = document.querySelector('#email').value; // trim de xoa khoang trang
    passwordResponse = document.querySelector('#password').value;
    console.info("lay thong tin");

    //login user
    let user = {
        email: emailResponse,
        password: passwordResponse,
        code: "abc"
    }

    let fetchData = {
        method: 'POST',
        body: JSON.stringify(user),
        headers: new Headers({
            'Content-Type': 'application/json; charset=UTF-8'
        })
    }
    const url = "/api/v1/users/login";
    console.log('Fetched from: ' + url)

    //get token
    try {
        const response = await fetch(url,fetchData);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const responseData = await response.json();
        const loginResponse = responseData['data'];
        token = loginResponse['token'];
        console.log(token);
    }catch(error) {
        console.error("Error fetching or parsing data:", error);
    }

    //handle
    usernamePage.classList.add('hidden')//khi enter room se an usernamePage di
    chatPage.classList.remove('hidden')//va hien chatPage

    const socket = new SockJS("/websocket");

    stompClient = Stomp.over(socket); //ket noi nguoi dung den websocket
    stompClient.connect({'Authorization': `Bearer ${token}`}, onConnected, onError);// thuc thi khi nguoi dung ket noi den websocket
    event.preventDefault();// ngăn chặn các xử lý mặc định của trình duyệt khi xảy ra sự kiện
}

//tien hanh dang ky nguoi dung vao hang doi tin nhan
 function onConnected() {
    console.log("start onConnected")
    stompClient.subscribe(`/user/${emailResponse}/queue/messages`, onMessageReceived,{'Authorization': `Bearer ${token}`});// /user là tiền tố đích của người dùng
    stompClient.subscribe('/topic/public', onMessageReceived,{'Authorization': `Bearer ${token}`});

    //set online status for user while login
    stompClient.send('/app/user.setOnlineStatus/'+emailResponse,{'Authorization': `Bearer ${token}`},//gui den endpoint
        JSON.stringify({email: emailResponse}));
    console.log("end send");

    document.querySelector('#connected-user-fullname').textContent = emailResponse;
    //tim va hien thi nhung ng dung da ket noi
    findAnDisplayConnectedUsers().then();
}

//tim va hien thi nhung ng dung da ket noi
async function findAnDisplayConnectedUsers(){ //phuong thuc khong dong bo - tra ve 1 loi hua - https://www.w3schools.com/js/js_async.asp
    // await lam cho ham tam dung viec thuc thi va cho doi 1 loi hua dc giai quyet trc khi tiep tuc
    const connectedUserResponse = await fetch(`/api/v1/users/find-connected-users`) //get mapping trong user controller
    let connectedUsers = await connectedUserResponse.json();
    console.log(connectedUsers)

    //loc di nguoi dung vua ket noi
    connectedUsers = connectedUsers.filter(user => user.email !== emailResponse);

    //khai bao nhung ng dung ket noi trong file html
    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML='';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
        if(connectedUsers.indexOf(user) < connectedUsers.length -1){
            //add a separator
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    })
}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
     listItem.classList.add('user-item');
     listItem.id = user.email;

     const userImage = document.createElement('img');
     userImage.src = '../img/user_icon.png';
     userImage.alt = user.email;

     const usernameSpan = document.createElement('span');
     usernameSpan.textContent = user.username;

     //nhan dang tin nhan ( da doc hay chua doc)
     const receivedMsgs = document.createElement('span');
     receivedMsgs.textContent = '0';
     receivedMsgs.classList.add('nbr-msg', 'hidden');

     listItem.appendChild(userImage);
     listItem.appendChild(usernameSpan);
     listItem.appendChild(receivedMsgs);

     listItem.addEventListener('click', listItemClick)

     connectedUsersList.appendChild(listItem);
}

function listItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget //vi tri event hien tai
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');//lay nickname cua user
    fetchAndDisplayUserChat().then();

    //khi nhan vao se an nbr-msg di
    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent='';
}

//Xu ly va hien thi khung chat cua ng dung
async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${emailResponse}/${selectedUserId}`);
    const userChat = await userChatResponse.json();

    chatArea.innerHTML = "";
    userChat.forEach(chat => {
        displayMessages(chat.senderId, chat.content);
    })
    chatArea.scrollTop = chatArea.scrollHeight;
}

//Hien thi tin nhan
function displayMessages(senderId, content) {
    console.log(senderId,content);

    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if(senderId === emailResponse){
        messageContainer.classList.add('sender');
    }else {
        messageContainer.classList.add('receiver');
    }

    const message = document.createElement('p');
    message.textContent = content;

    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

function onError() {

}
async function onMessageReceived(payload) {
    await findAnDisplayConnectedUsers();
    const message = JSON.parse(payload.body);
    if(selectedUserId && selectedUserId === message.senderId){
        displayMessages(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    //neu select User thi se active
    if(selectedUserId){
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    }else {// neu ko se an thanh dien va gui message
        messageForm.classList.add('hidden');
    }

    //nhan thong bao tin nhan gui den
    const notifiedUser = document.querySelector(`#${message.senderId}`)
    if(!notifiedUser.classList.contains('active')){
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent='';
    }
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient){
        const chatMessage = {
            id: "",
            senderId: emailResponse,
            recipientId: selectedUserId,
            content: messageContent,
            timestamp: new Date()
        }
        stompClient.send('/app/chat',{'Authorization': `Bearer ${token}`}, JSON.stringify(chatMessage));
        console.log("send message"+chatMessage);
        displayMessages(emailResponse, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}
function onLogout() {
    stompClient.send('/app/user.disconnectUser/'+emailResponse, {},
        JSON.stringify({email: emailResponse}));

    //reload window de dang nhap (hoac co the remove hidden thu cong)
    window.location.reload();

}
usernameForm.addEventListener('submit', connect, true); // khi submit se chay function connect
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
//logout user khi reload page
window.onbeforeunload = () => onLogout();
