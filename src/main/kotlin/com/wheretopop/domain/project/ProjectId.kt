package com.wheretopop.domain.project

import com.wheretopop.shared.model.UniqueId

class ProjectId private constructor(
    value: Long
) : UniqueId(value) {
    companion object {
        @JvmStatic
        fun create(): ProjectId {
            return ProjectId(UniqueId.create().value)
        }

        @JvmStatic
        fun of(value: Long): ProjectId {
            return ProjectId(UniqueId.of(value).value)
        }

        @JvmStatic
        fun of(value: String): ProjectId {
            return ProjectId(UniqueId.of(value).value)
        }
    }
}
