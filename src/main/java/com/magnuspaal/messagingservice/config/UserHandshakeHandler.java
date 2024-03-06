package com.magnuspaal.messagingservice.config;

import com.magnuspaal.messagingservice.auth.JwtService;
import com.magnuspaal.messagingservice.user.User;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.*;

@RequiredArgsConstructor
public class UserHandshakeHandler extends DefaultHandshakeHandler {

  private final JwtService jwtService;

  @Override
  protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
    Iterator cookies = Arrays.stream(request.getHeaders().get("cookie").get(0).split("; ")).iterator();
    String jwt = null;

    while(cookies.hasNext()) {
      String next = cookies.next().toString();
      if (next.startsWith("authToken=")) {
        jwt = next.substring(10);
        break;
      }
    }

    Authentication authentication = jwtService.validateJWT(jwt);

    User user = (User) authentication.getPrincipal();
    UserPrincipal userPrincipal = new UserPrincipal(user.getId().toString());

    return userPrincipal;
  }
}
