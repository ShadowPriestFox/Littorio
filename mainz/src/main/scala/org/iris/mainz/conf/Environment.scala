package org.iris.mainz.conf

import pureconfig.* 
import pureconfig.generic.derivation.EnumConfigReaderDerivation.Default.EnumConfigReader
import pureconfig.generic.derivation.default.*

enum Environment derives EnumConfigReader:
  case Local,Alpha,Prod
