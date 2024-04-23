package org.iris.mainz.conf
import pureconfig.*
import pureconfig.generic.derivation.default.*

final case class ServerConf(env: Environment, db: DBConf) derives ConfigReader
