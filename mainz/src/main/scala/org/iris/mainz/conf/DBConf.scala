package org.iris.mainz.conf

import pureconfig.* 
import pureconfig.generic.derivation.default.*

final case class DBConf(username: String,password: String,url: String) derives ConfigReader
