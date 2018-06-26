package daggerok.app

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ConferenceQueryModel(@Id val id: String? = null,
                                val name: String? = null)

interface ConferenceQueryModelRepository : JpaRepository<ConferenceQueryModel, String>

@Entity
data class SpeakerQueryModel(@Id val id: String? = null,
                             @ElementCollection val talks: MutableList<String>? = mutableListOf())

interface SpeakerQueryModelRepository : JpaRepository<SpeakerQueryModel, String>

/**/
class SpeakerNotFoundException(override val message: String? = "Speaker wasn't found.") : RuntimeException()
