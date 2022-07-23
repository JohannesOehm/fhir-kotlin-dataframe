import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Patient
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import java.io.File

fun main() {
    val r4 = FhirContext.forR4()
    val client = r4.newRestfulGenericClient("https://vonk.fire.ly/r4")
    var patients = client.search<Bundle>().forResource(Patient::class.java).count(10).execute()
    val text = buildString {
        while (patients != null) {
            append(r4.newNDJsonParser().encodeResourceToString(patients))
            patients = try {
                client.loadPage().next(patients).execute()
            } catch (e: java.lang.IllegalArgumentException) {
                null
            }
        }
    }
    val df = DataFrame.readJsonStr(text.lines().joinToString(",", "[", "]"))
    df.print()


}