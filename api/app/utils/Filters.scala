package utils

import javax.inject.Inject

import modules.CorsFilter
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter

/**
 * Provides filters.
 */
class Filters @Inject() (securityHeadersFilter: SecurityHeadersFilter, corsFilter: CorsFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(corsFilter, securityHeadersFilter)
}
