package application

import declslides.application.ApplicationError
import declslides.application.InMemoryPresentationRegistry
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable
import org.scalatest.flatspec.AnyFlatSpec

class InMemoryPresentationRegistrySpec
    extends AnyFlatSpec
    with ApplicationSpecSupport:

  behavior of "InMemoryPresentationRegistry"

  it should "list available presentation names in sorted order" in:
    val registry =
      InMemoryPresentationRegistry(
        "zeta" -> sampleDeck,
        "alpha" -> sampleDeck,
      )

    registry.available.shouldBe(Vector("alpha", "zeta"))

  it should "resolve a known presentation" in:
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)

    val result = registry.resolve("demo").value

    result.title.shouldBe("Demo")

  it should "fail for an unknown presentation" in:
    val registry = InMemoryPresentationRegistry("demo" -> sampleDeck)

    registry.resolve("missing").left.value.shouldBe(
      ApplicationError.PresentationNotFound("missing"),
    )
