package declslides.dsl

import declslides.domain._

object DSL:

  final case class PendingSlide(
    title: String,
    layout: Layout,
    elements: Vector[SlideElement])

  final case class PresentationState(
    title: String,
    theme: Theme = Theme.default,
    pendingSlides: Vector[PendingSlide] = Vector.empty)

  final case class SlideState(
    title: String,
    layout: Layout,
    elements: Vector[SlideElement] = Vector.empty)

  final case class PresBuild(
    run: PresentationState => PresentationState):

    private infix def andThen(other: PresBuild): PresBuild =
      PresBuild(state => other.run(run(state)))

  private object PresBuild:

    private val empty: PresBuild =
      PresBuild(identity)

    def combineAll(builds: Seq[PresBuild]): PresBuild =
      builds.foldLeft(empty)(_ andThen _)

  final case class SlideBuild(
    run: SlideState => SlideState):

    private infix def andThen(other: SlideBuild): SlideBuild =
      SlideBuild(state => other.run(run(state)))

  private object SlideBuild:

    private val empty: SlideBuild =
      SlideBuild(identity)

    def combineAll(builds: Seq[SlideBuild]): SlideBuild =
      builds.foldLeft(empty)(_ andThen _)

  def deck(items: PresBuild*): PresBuild =
    PresBuild.combineAll(items)

  def content(items: SlideBuild*): SlideBuild =
    SlideBuild.combineAll(items)

  def presentation(title: String)(body: => PresBuild)
    : Either[Vector[DomainError], Presentation] =
    val finalState =
      body.run(PresentationState(title = title))

    val (slideErrors, validSlides) =
      validateSlides(finalState.pendingSlides)

    val presentationErrors =
      Presentation.validateSkeleton(
        title = finalState.title,
        slideTitles = finalState.pendingSlides.map(_.title),
      )

    val allErrors =
      slideErrors ++ presentationErrors

    if allErrors.nonEmpty then
      Left(allErrors)
    else
      Presentation(finalState.title, validSlides, finalState.theme)

  def theme(value: Theme): PresBuild =
    PresBuild(state => state.copy(theme = value))

  def slide(
    title: String,
    layout: Layout = Layout.Flow,
  )(body: => SlideBuild,
  ): PresBuild =
    PresBuild { state =>
      val slideState =
        body.run(SlideState(title = title, layout = layout))

      val pending =
        PendingSlide(
          title = slideState.title,
          layout = slideState.layout,
          elements = slideState.elements,
        )

      state.copy(
        pendingSlides = state.pendingSlides :+ pending,
      )
    }

  def text(value: String): SlideBuild =
    SlideBuild { state =>
      state.copy(
        elements = state.elements :+ SlideElement.Paragraph(value),
      )
    }

  def bullets(items: String*): SlideBuild =
    SlideBuild { state =>
      state.copy(
        elements = state.elements :+ SlideElement.BulletList(items.toVector),
      )
    }

  def code(
    language: String,
    source: String,
  ): SlideBuild =
    SlideBuild { state =>
      state.copy(
        elements = state.elements :+ SlideElement.CodeBlock(language, source),
      )
    }

  def spacer(lines: Int = 1): SlideBuild =
    SlideBuild { state =>
      state.copy(
        elements = state.elements :+ SlideElement.Spacer(lines),
      )
    }

  private def validateSlides(
    pendingSlides: Vector[PendingSlide],
  ): (Vector[DomainError], Vector[Slide]) =
    val results =
      pendingSlides.map(ps => Slide(ps.title, ps.elements, ps.layout))

    val errors =
      results.collect { case Left(errs) => errs }.flatten

    val validSlides =
      results.collect { case Right(slide) => slide }

    (errors, validSlides)
