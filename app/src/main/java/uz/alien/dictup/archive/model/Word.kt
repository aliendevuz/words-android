package uz.alien.dictup.archive.model

data class Word(
  val w: String,  // word
  val t: String,  // transcript
  val ty: String, // type
  val d: String,  // description
  val s: String   // sample
)