package daggerok.app

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateConferenceCommand(val id: String, val name: String)
data class ConferenceCreatedEvent(val id: String, val name: String)

data class AddTalkCommand(@TargetAggregateIdentifier val id: String, val talk: String, val speaker: String)
data class TalkAddedEvent(val id: String, val talk: String, val speaker: String)

/**/

class DuplicatedTaskException(override val message: String) : RuntimeException()
