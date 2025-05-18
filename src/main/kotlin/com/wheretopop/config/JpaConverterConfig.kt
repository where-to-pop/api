package com.wheretopop.config

import com.wheretopop.domain.building.BuildingId
import com.wheretopop.domain.building.register.BuildingRegisterId
import com.wheretopop.domain.chat.ChatId
import com.wheretopop.domain.chat.ChatMessageId
import com.wheretopop.domain.popup.PopupId
import com.wheretopop.domain.project.ProjectId
import com.wheretopop.domain.user.UserId
import com.wheretopop.domain.user.auth.AuthUserId
import com.wheretopop.domain.user.auth.Password
import com.wheretopop.domain.user.auth.RefreshTokenId
import com.wheretopop.shared.domain.identifier.AreaId
import com.wheretopop.shared.domain.identifier.AreaPopulationId
import com.wheretopop.shared.domain.identifier.PopupPopplyId
import com.wheretopop.shared.domain.identifier.XId
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter


@Converter(autoApply = false)
class PasswordConverter : AttributeConverter<Password, String> {
    override fun convertToDatabaseColumn(attribute: Password?): String? {
        return attribute?.hashed
    }

    override fun convertToEntityAttribute(dbData: String?): Password? {
        return dbData?.let { Password.fromHashed(it) }
    }
}


// 지역(Area) ID 변환기
@Converter(autoApply = false)
class AreaIdConverter : AttributeConverter<AreaId, Long> {
    override fun convertToDatabaseColumn(attribute: AreaId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): AreaId? {
        return dbData?.let { AreaId.of(it) }
    }
}

@Converter(autoApply = false)
class AreaPopulationIdConverter : AttributeConverter<AreaPopulationId, Long> {
    override fun convertToDatabaseColumn(attribute: AreaPopulationId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): AreaPopulationId? {
        return dbData?.let { AreaPopulationId.of(it) }
    }
}

// 빌딩(Building) ID 변환기
@Converter(autoApply = false)
class BuildingIdConverter : AttributeConverter<BuildingId, Long> {
    override fun convertToDatabaseColumn(attribute: BuildingId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): BuildingId? {
        return dbData?.let { BuildingId.of(it) }
    }
}

// 빌딩 등록(BuildingRegister) ID 변환기
@Converter(autoApply = false)
class BuildingRegisterIdConverter : AttributeConverter<BuildingRegisterId, Long> {
    override fun convertToDatabaseColumn(attribute: BuildingRegisterId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): BuildingRegisterId? {
        return dbData?.let { BuildingRegisterId.of(it) }
    }
}

// 채팅(Chat) ID 변환기
@Converter(autoApply = false)
class ChatIdConverter : AttributeConverter<ChatId, Long> {
    override fun convertToDatabaseColumn(attribute: ChatId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): ChatId? {
        return dbData?.let { ChatId.of(it) }
    }
}

// 채팅 메시지(ChatMessage) ID 변환기
@Converter(autoApply = false)
class ChatMessageIdConverter : AttributeConverter<ChatMessageId, Long> {
    override fun convertToDatabaseColumn(attribute: ChatMessageId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): ChatMessageId? {
        return dbData?.let { ChatMessageId.of(it) }
    }
}

// 팝업(Popup) ID 변환기
@Converter(autoApply = false)
class PopupIdConverter : AttributeConverter<PopupId, Long> {
    override fun convertToDatabaseColumn(attribute: PopupId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): PopupId? {
        return dbData?.let { PopupId.of(it) }
    }
}

@Converter(autoApply = false)
class XIdConverter : AttributeConverter<XId, Long> {
    override fun convertToDatabaseColumn(attribute: XId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): XId? {
        return dbData?.let { XId.of(it) }
    }
}

@Converter(autoApply = false)
class PopupPopplyIdConverter : AttributeConverter<PopupPopplyId, Long> {
    override fun convertToDatabaseColumn(attribute: PopupPopplyId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): PopupPopplyId? {
        return dbData?.let { PopupPopplyId.of(it) }
    }
}


// 프로젝트(Project) ID 변환기
@Converter(autoApply = false)
class ProjectIdConverter : AttributeConverter<ProjectId, Long> {
    override fun convertToDatabaseColumn(attribute: ProjectId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): ProjectId? {
        return dbData?.let { ProjectId.of(it) }
    }
}

// 사용자(User) ID 변환기
@Converter(autoApply = false)
class UserIdConverter : AttributeConverter<UserId, Long> {
    override fun convertToDatabaseColumn(attribute: UserId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): UserId? {
        return dbData?.let { UserId.of(it) }
    }
}

// 인증 사용자(AuthUser) ID 변환기
@Converter(autoApply = false)
class AuthUserIdConverter : AttributeConverter<AuthUserId, Long> {
    override fun convertToDatabaseColumn(attribute: AuthUserId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): AuthUserId? {
        return dbData?.let { AuthUserId.of(it) }
    }
}

// 리프레시 토큰(RefreshToken) ID 변환기
@Converter(autoApply = false)
class RefreshTokenIdConverter : AttributeConverter<RefreshTokenId, Long> {
    override fun convertToDatabaseColumn(attribute: RefreshTokenId?): Long? {
        return attribute?.toLong()
    }

    override fun convertToEntityAttribute(dbData: Long?): RefreshTokenId? {
        return dbData?.let { RefreshTokenId.of(it) }
    }
}
