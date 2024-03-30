
import cats.parse.Parser
import cats.parse.Parser.char
import cats.parse.Rfc5234.{alpha, sp, wsp}

val leftSide = ((char('/').string) ~ alpha.rep.string).string
val finalSide = sp *> Parser.anyChar.rep.string 

val parser = (leftSide ~ finalSide.?)//.map((left,right) => left -> right.map((_, str, _) => str))
val whisperParser = (alpha.rep.string <* sp) ~ Parser.anyChar.rep.string 
parser.parseAll("/help")
parser.parseAll("/name hxx fjiowe")
parser.parseAll("/whisper hxx 123ji12josdnijfioew@(812) jifiewad jfowda")
  .flatMap{
    case ("/whisper", opt) => opt match
      case None => ???
      case Some(value) => whisperParser.parse(value)
    case (cmd, opt) => ???
  }
