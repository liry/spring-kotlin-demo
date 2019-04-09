package cz.liry.springkotlindemo

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@SpringBootApplication
class SpringKotlinDemoApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
            .sources(SpringKotlinDemoApplication::class.java)
            .initializers(beans {
                bean {
                    ApplicationRunner {
                        val repository = ref<CustomerRepository>()
                        arrayOf("Jirka", "Martin", "Libor")
                                .map { Customer(name = it) }
                                .forEach { repository.save(it) }
                        repository.findAll().forEach { println(it) }
                    }
                }
            })
            .run(*args)
}

@RestController
@CrossOrigin("*")
class CustomerRestController(private val repository: CustomerRepository) {

    @GetMapping("/customers")
    fun customers() = repository.findAll()

    @PostMapping("/customers")
    fun addCustomer(@RequestBody customer: Customer) {
        repository.save(customer)
    }
}

@Repository
interface CustomerRepository : CrudRepository<Customer, Long>

@Entity
data class Customer(val name: String, @Id @GeneratedValue var id: Long? = null)
