package com.dkhang.testmeetevent.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.dkhang.testmeetevent.services.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/queue","/user");
        config.setApplicationDestinationPrefixes("/app");
        // config.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").withSockJS();
//        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                log.debug("Headers: {}", accessor);
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
//                    if (authorizationHeader == null)
//                        throw new RuntimeException("Access Denied");
//                    final String token = authorizationHeader;
//                    final String userEmail = jwtService.extractUsername(token);
//                    if (userEmail != null) {
//                        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//                        if (jwtService.isTokenValid(token, userDetails)) {
//                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null, userDetails.getAuthorities());
//                            // SecurityContextHolder.getContext().setAuthentication(authToken);
//                            accessor.setUser(authToken);
//                        }
//                    }
//                }
//                return message;
//            }
//        });
//    }
@Override
public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {

//        @Override
//        public Message<?> preSend(Message<?> message, MessageChannel channel) {
//            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//            MessageHeaders headers = message.getHeaders();
//            SimpMessageType type = (SimpMessageType) headers.get("simpMessageType");
//            List<String> tokenList = accessor.getNativeHeader("Authorization");
//            String token = null;
//            if(tokenList == null || tokenList.size() < 1) {
//                return message;
//            } else {
//                token = tokenList.get(0);
//                if(token == null) {
//                    return message;
//                }
//            }
//
//            // validate and convert to a Principal based on your own requirements e.g.
//            // authenticationManager.authenticate(JwtAuthentication(token))
//            final String userEmail = jwtService.extractUsername(token);
//            if (userEmail != null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//                if (jwtService.isTokenValid(token, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null, userDetails.getAuthorities());
//                            // SecurityContextHolder.getContext().setAuthentication(authToken);
//                            accessor.setUser(authToken);
//                }
//            }
//
//            // not documented anywhere but necessary otherwise NPE in StompSubProtocolHandler!
//            accessor.setLeaveMutable(true);
//            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
//        }
@Override
public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//Authenticate user on CONNECT
    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
//Extract JWT token from header, validate it and extract user authorities
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer" + " ")) {
// If there is no token present then we should interrupt handshake process and throw an
//            AccessDeniedException
//            throw new AccessDeniedException(WebSocketSecurityConfig.WS_UNAUTHORIZED_MESSAGE);
            log.info("error message");
        }
        String token = authHeader.substring("Bearer".length() + 1);
        final String userEmail = jwtService.extractUsername(token);
        if (userEmail != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, userDetails.getAuthorities());
                // SecurityContextHolder.getContext().setAuthentication(authToken);
                accessor.setUser(authToken);
            }
        }
    }
    // not documented anywhere but necessary otherwise NPE in StompSubProtocolHandler!
    accessor.setLeaveMutable(true);
    return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
}
    });

}
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver(); //trình giải quyết
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);

        return false;//cần trả về false để registerDefault=false (f5 phương thức để xem chi tiết)
    }
    @Bean
    public MappingJackson2MessageConverter mappingJackson2MessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
        jacksonMessageConverter.setObjectMapper(objectMapper);
        jacksonMessageConverter.setSerializedPayloadClass(String.class);
        jacksonMessageConverter.setStrictContentTypeMatch(true);
        return jacksonMessageConverter;
    }
}
