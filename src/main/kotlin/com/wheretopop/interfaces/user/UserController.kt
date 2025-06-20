package com.wheretopop.interfaces.user

import com.wheretopop.application.user.UserFacade
import com.wheretopop.application.user.UserInput
import com.wheretopop.config.security.CurrentUser
import com.wheretopop.config.security.UserPrincipal
import com.wheretopop.shared.exception.toException
import com.wheretopop.shared.response.CommonResponse
import com.wheretopop.shared.response.ErrorCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 사용자(User) 관련 컨트롤러
 * Spring MVC 기반으로 구현
 */
@RestController
@RequestMapping("/v1/users")
class UserController(private val userFacade: UserFacade) {
    
    /**
     * 회원 가입 요청을 처리합니다.
     */
    @PostMapping
    fun signUp(@RequestBody signUpRequest: UserDto.SignUpRequest): ResponseEntity<CommonResponse<UserDto.UserResponse>> {
        val input = UserInput.SignUp(
            username = signUpRequest.username,
            email = signUpRequest.email,
            identifier = signUpRequest.identifier,
            rawPassword = signUpRequest.password,
            profileImageUrl = signUpRequest.profileImageUrl,
        )

        // NOTE: 회원가입 비활성화함
        throw ErrorCode.COMMON_FORBIDDEN.toException()
//        val userInfo = userFacade.signUp(input)
//        val generation = UserDto.UserResponse.from(userInfo)
//
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(CommonResponse.success(generation))
    }
    
    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     * Spring Security를 통해 인증된 사용자만 접근 가능합니다.
     */
    @GetMapping("/me")
    fun getCurrentUser(@CurrentUser principal: UserPrincipal): ResponseEntity<CommonResponse<String>> {
        val userId = principal.userId
        
        // TODO: userFacade에 findUserById 메서드를 구현하고 호출
        // val userInfo = userFacade.findUserById(userId)
        
        // 임시 응답 (실제로는 사용자 정보를 조회하여 반환)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(CommonResponse.success("사용자 ID: ${userId.toLong()}"))
    }
}