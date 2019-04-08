package customer

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.*
import react.dom.*
import kotlin.browser.*
import kotlin.js.Json

interface CustomerProps : RProps {

}

interface CustomerState : RState {
    var customers: List<Customer>?
    var newName: String

}

class Customers(props: CustomerProps) : RComponent<CustomerProps, CustomerState>(props) {

    override fun componentDidMount() {
        fetchCustomers()
    }

    override fun RBuilder.render() {
        if (state.customers == null) {
            p {
                +"No customers"
            }
        } else {
            table {
                tbody {
                    state.customers!!.forEach { c ->
                        tr {
                            td {
                                + c.name
                            }
                            td {
                                + "${c.id!!}"
                            }
                        }
                    }
                }
            }
        }
        input(type = InputType.text, name = "itemText") {
            key = "itemText"

            attrs {
                value = state.newName
                placeholder = "New customer name"
                onChangeFunction = {
                    val target = it.target as HTMLInputElement
                    setState {
                        newName = target.value
                    }
                }
            }
        }

        button {
            +"Add customer"
            attrs {
                onClickFunction = {
                    if (state.newName.isNotEmpty()) {
                        window.fetch(CUSTOMERS_URL, RequestInit(method = "POST", body = """{"name": "${state.newName}"}""", headers = HEADERS)).then {
                            fetchCustomers()
                        }
                        setState {
                            newName = ""
                        }
                    }
                }
            }
        }
    }


    fun fetchCustomers() {
        window.fetch(CUSTOMERS_URL).then { resp ->
            resp.json().then { respJson: dynamic ->
                setState {
                    customers = respJson.unsafeCast<Array<Json>>().map { json ->
                        Customer(json["name"].unsafeCast<String>(), json["id"].unsafeCast<Long>())
                    }
                }
            }
        }
    }

    companion object {
        private const val CUSTOMERS_URL = "http://localhost:8080/customers"
        private val HEADERS = Headers().apply {
            set("Content-Type", "application/json")
            set("Accept", "application/json")
        }
    }
}

fun RBuilder.customers() = child(Customers::class) {
}


data class Customer(val name: String, val id: Long? = null)


//external interface A(val b: String, val c: Array<String>)