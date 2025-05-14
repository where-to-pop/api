package com.wheretopop.interfaces.user

import com.wheretopop.application.user.UserFacade
import com.wheretopop.application.user.UserInput
import com.wheretopop.config.security.AUTH_GET
import com.wheretopop.domain.user.UserId
import com.wheretopop.shared.response.CommonResponse
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

/**
 * 사용자(User) 관련 라우터 정의
 * Spring WebFlux 함수형 엔드포인트 사용
 */
@Configuration
class UserApiRouter(private val userHandler: UserHandler): RouterFunction<ServerResponse> {

    private val delegate = coRouter {
        "/v1/users".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                // 회원 가입 (인증 불필요)
                POST("", userHandler::signUp)
                
                // 사용자 정보 조회 (인증 필요)
                AUTH_GET("/me") { request, userId ->
                    userHandler.getCurrentUser(request, userId)
                }
            }
        }
    }
    
    override fun route(request: ServerRequest): Mono<HandlerFunction<ServerResponse>> = delegate.route(request)
}

/**
 * 사용자(User) 관련 요청 처리 핸들러
 */
@Component
class UserHandler(private val userFacade: UserFacade) {
    
    /**
     * 회원 가입 요청을 처리합니다.
     */
    suspend fun signUp(request: ServerRequest): ServerResponse {
        val signUpRequest = request.awaitBody<UserDto.SignUpRequest>()
        
        val input = UserInput.SignUp(
            username = signUpRequest.username,
            email = signUpRequest.email,
            identifier = signUpRequest.identifier,
            rawPassword = signUpRequest.password,
            profileImageUrl = signUpRequest.profileImageUrl,
        )

        val userInfo = userFacade.signUp(input)
        val response = UserDto.UserResponse.from(userInfo)
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success(response))
    }
    
    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     * Spring Security를 통해 인증된 사용자만 접근 가능합니다.
     */
    suspend fun getCurrentUser(request: ServerRequest, userId: UserId): ServerResponse {
        // TODO: userFacade에 findUserById 메서드를 구현하고 호출
        // val userInfo = userFacade.findUserById(userId)
        
        // 임시 응답 (실제로는 사용자 정보를 조회하여 반환)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(CommonResponse.success("사용자 ID: ${userId.toLong()}"))
    }
}