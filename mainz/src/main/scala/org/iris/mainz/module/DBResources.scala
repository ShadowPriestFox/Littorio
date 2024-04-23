package org.iris.mainz.module

import cats.effect.Async
import org.iris.mainz.conf.DBConf
import cats.effect.kernel.Resource
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor

object DBResources:
  def make[F[_]: Async](conf: DBConf) = 
    val xa = for 
      hikariConfig <- Resource.pure({
        val config = new HikariConfig()
        config.setDriverClassName("com.mysql.cj.jdbc.Driver")
        config.setJdbcUrl(conf.url)
        config.setUsername(conf.username)
        config.setPassword(conf.password)
        config
      })
      txa <- HikariTransactor.fromHikariConfig[F](hikariConfig)
    yield txa
    xa.map(x => (x, x.kernel))
