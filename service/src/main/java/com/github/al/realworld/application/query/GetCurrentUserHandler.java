package com.github.al.realworld.application.query;

import com.github.al.realworld.api.query.GetCurrentUser;
import com.github.al.realworld.api.query.GetCurrentUserResult;
import com.github.al.realworld.application.UserAssembler;
import com.github.al.realworld.application.service.JwtService;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;

@RequiredArgsConstructor
@Singleton
public class GetCurrentUserHandler implements QueryHandler<GetCurrentUserResult, GetCurrentUser> {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public GetCurrentUserResult handle(GetCurrentUser query) {
        User user = userRepository.findByUsername(query.getUsername())
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", query.getUsername()));

        return new GetCurrentUserResult(UserAssembler.assemble(user, jwtService));
    }
}
