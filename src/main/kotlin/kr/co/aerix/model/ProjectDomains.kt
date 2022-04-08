package kr.co.aerix.model

import java.time.LocalDateTime

data class Project_domain(
    val id: Int,
    val name: String
);

data class Project_Req(
    val id: Int,
    val name: String
);

data class Project_Patch_Req(
    val id: Int,
    val mode: Boolean
);