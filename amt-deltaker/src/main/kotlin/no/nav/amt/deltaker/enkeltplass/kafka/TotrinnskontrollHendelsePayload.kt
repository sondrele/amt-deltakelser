package no.nav.amt.deltaker.enkeltplass.kafka

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant
import java.util.UUID

data class TotrinnskontrollHendelsePayload(
    val id: UUID, // Random id(for ukjente meldingstyper), eller totrinnskontrollRefId på EndrePrisinformasjon/EnkeltplassSoktInn
    val entityId: UUID, // gjennomføringID(key på topicen)
    val type: TotrinnskontrollType,
    val behandletAv: TotrinnskontrollAgent,
    val behandletTidspunkt: Instant,
    val besluttetAv: TotrinnskontrollAgent?,
    val besluttetTidspunkt: Instant?,
    val besluttelse: TotrinnskontrollBesluttelse?,
    val aarsaker: List<String>,
    val forklaring: String?, // Kan sette på vent (avvise) med forklaring
) {
    enum class TotrinnskontrollType {
        TILSAGN_OPPRETTELSE,
        TILSAGN_ANNULLERING,
        TILSAGN_OPPGJOR,
        UTBETALING_LINJE_OPPRETTELSE,
        ENKELTPLASS_OKONOMI, // Mottas når deltakelsen godkjennes

        // ENKELTPLASS_GODKJENN_PRISINFORMASJON, // Mottas når prisinformasjon godkjennes
        TILSKUDD_OPPRETTELSE,
    }

    enum class TotrinnskontrollBesluttelse {
        GODKJENT,
        AVVIST, // Sett på vent
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(
        JsonSubTypes.Type(value = TotrinnskontrollAgent.NavAnsatt::class, name = "NAV_ANSATT"),
        JsonSubTypes.Type(value = TotrinnskontrollAgent.System::class, name = "SYSTEM"),
        JsonSubTypes.Type(value = TotrinnskontrollAgent.Arrangor::class, name = "ARRANGOR"),
    )
    sealed interface TotrinnskontrollAgent {
        data class NavAnsatt(
            val navIdent: String,
        ) : TotrinnskontrollAgent

        data class System(
            val system: String,
        ) : TotrinnskontrollAgent

        data object Arrangor : TotrinnskontrollAgent
    }
}
