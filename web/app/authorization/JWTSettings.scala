package authorization

import play.api.Configuration

class JWTSettings(config: Configuration) {
  def headerName: String = config.getString("authenticator.headerName").get
  def sharedSecret: String = config.getString("authenticator.sharedSecret").get
}
