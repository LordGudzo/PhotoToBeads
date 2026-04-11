package com.lordgudzo.phototobeads.data.mapper

import com.lordgudzo.phototobeads.data.storage.palettestorage.models.BeadColorDto
import com.lordgudzo.phototobeads.domain.model.BeadColor

fun BeadColorDto.toDomain(): BeadColor {
    return BeadColor(
        code = code,
        name = name,
        r = r,
        g = g,
        b = b
    )
}

fun List<BeadColorDto>.toDomain(): List<BeadColor> {
    return map { it.toDomain() }
}

//fun beadColorDtoToDomain(beadColorDto: BeadColorDto): BeadColor {
//    return BeadColor(
//        code = beadColorDto.code,
//        name = beadColorDto.name,
//        r = beadColorDto.r,
//        g = beadColorDto.g,
//        b = beadColorDto.b
//    )
//}
//fun listBeadColorDtoToDomain(listBeadColorDto: List<BeadColorDto> ): List<BeadColor> {
//    val result = mutableListOf<BeadColor>()
//    listBeadColorDto.forEach { dto ->
//        result.add(dto.toDomain())
//    }
//    return result
//}