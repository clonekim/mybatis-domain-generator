package krud

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
class DemoApplication {
	companion object:KtLog()

	fun main(args: Array<String>) {
		runApplication<DemoApplication>(*args)
	}


	@Profile("linux")
	@PostConstruct
	fun setTimeZone() {
		log.debug("[linux profile] timeZone to Asia/Seoul")
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
	}

}

@Configuration
class WebConfig: WebMvcConfigurer {

	override fun addViewControllers(registry: ViewControllerRegistry) {
		registry.addViewController("/").setViewName("index.html")
	}

	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/static/")
				.setCachePeriod(0)
				.setCacheControl(CacheControl.noCache())
				.resourceChain(true)
				.addResolver(PathResourceResolver())
	}
}