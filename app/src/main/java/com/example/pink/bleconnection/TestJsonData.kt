package com.example.pink.bleconnection

data class TestingData(
    val glossary: Glossary
)

data class Glossary(
    val GlossDiv: GlossDiv,
    val title: String
)

data class GlossDiv(
    val GlossList: GlossList,
    val title: String
)

data class GlossList(
    val GlossEntry: GlossEntry
)

data class GlossEntry(
    val Abbrev: String,
    val Acronym: String,
    val GlossDef: GlossDef,
    val GlossSee: String,
    val GlossTerm: String,
    val ID: String,
    val SortAs: String
)

data class GlossDef(
    val GlossSeeAlso: List<String>,
    val para: String
)
