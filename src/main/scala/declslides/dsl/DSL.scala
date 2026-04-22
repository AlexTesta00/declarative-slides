package declslides.dsl

import declslides.domain._

object DSL:

  final case class PendingSlide(
    title: String,
    layout: Layout,
    elements: Vector[SlideElement])

  final case class PresentationConfig(
    title: String,
    theme: Theme)

  final case class PresentationState(
    title: String,
    theme: Theme = Theme.default,
    pendingSlides: Vector[PendingSlide] = Vector.empty):

    def appendSlide(slide: PendingSlide): PresentationState =
      copy(pendingSlides = pendingSlides :+ slide)

  final case class SlideState(
    title: String,
    layout: Layout,
    elements: Vector[SlideElement] = Vector.empty):

    def appendElement(element: SlideElement): SlideState =
      copy(elements = elements :+ element)

  final class PresentationStart(title: String):

    def apply(
      body: => PresBuild,
    ): Either[Vector[DomainError], Presentation] =
      configured(theme = Theme.default)(body)

    infix def use(
      theme: Theme,
    ): ConfiguredPresentation =
      configured(theme)

    private def configured(
      theme: Theme,
    ): ConfiguredPresentation =
      new ConfiguredPresentation(
        PresentationConfig(
          title = title,
          theme = theme,
        ),
      )

  final class ConfiguredPresentation(
    config: PresentationConfig):

    def apply(
      body: => PresBuild,
    ): Either[Vector[DomainError], Presentation] =
      PresentationBuilder.build(
        config = config,
        body = body,
      )

  final case class PresBuild(
    run: PresentationState => PresentationState):

    private infix def andThen(other: PresBuild): PresBuild =
      PresBuild(
        StateTransform.andThen(
          left = run,
          right = other.run,
        ),
      )

  private object PresBuild:

    private val empty: PresBuild =
      PresBuild(StateTransform.identityOf)

    def combineAll(builds: Seq[PresBuild]): PresBuild =
      PresBuild(
        StateTransform.combineAll(
          builds.map(_.run),
        ),
      )

  final case class SlideBuild(
    run: SlideState => SlideState):

    private infix def andThen(other: SlideBuild): SlideBuild =
      SlideBuild(
        StateTransform.andThen(
          left = run,
          right = other.run,
        ),
      )

  private object SlideBuild:

    private val empty: SlideBuild =
      SlideBuild(StateTransform.identityOf)

    def combineAll(builds: Seq[SlideBuild]): SlideBuild =
      SlideBuild(
        StateTransform.combineAll(
          builds.map(_.run),
        ),
      )

  private object StateTransform:

    def identityOf[S]: S => S =
      state => state

    def andThen[S](
      left: S => S,
      right: S => S,
    ): S => S =
      state => right(left(state))

    def combineAll[S](
      transforms: Seq[S => S],
    ): S => S =
      transforms.foldLeft(identityOf[S])(andThen)

  private object PresentationBuilder:

    def build(
      config: PresentationConfig,
      body: => PresBuild,
    ): Either[Vector[DomainError], Presentation] =
      val finalState =
        body.run(initialState(config))

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
        Presentation(
          title = finalState.title,
          slides = validSlides,
          theme = finalState.theme,
        )

    private def initialState(
      config: PresentationConfig,
    ): PresentationState =
      PresentationState(
        title = config.title,
        theme = config.theme,
      )

  def presentation(title: String): PresentationStart =
    new PresentationStart(title)

  def deck(items: PresBuild*): PresBuild =
    PresBuild.combineAll(items)

  def content(items: SlideBuild*): SlideBuild =
    SlideBuild.combineAll(items)

  def slide(
    title: String,
    layout: Layout = Layout.Flow,
  )(body: => SlideBuild,
  ): PresBuild =
    PresBuild { state =>
      val slideState =
        body.run(
          SlideState(
            title = title,
            layout = layout,
          ),
        )

      state.appendSlide(
        toPendingSlide(slideState),
      )
    }

  def text(value: String): SlideBuild =
    appendElement(
      SlideElement.Paragraph(value),
    )

  def bullets(items: String*): SlideBuild =
    appendElement(
      SlideElement.BulletList(items.toVector),
    )

  def code(
    language: String,
    source: String,
  ): SlideBuild =
    appendElement(
      SlideElement.CodeBlock(language, source),
    )

  def spacer(lines: Int = 1): SlideBuild =
    appendElement(
      SlideElement.Spacer(lines),
    )

  private def appendElement(
    element: SlideElement,
  ): SlideBuild =
    SlideBuild(state => state.appendElement(element))

  private def toPendingSlide(
    state: SlideState,
  ): PendingSlide =
    PendingSlide(
      title = state.title,
      layout = state.layout,
      elements = state.elements,
    )

  private def validateSlides(
    pendingSlides: Vector[PendingSlide],
  ): (Vector[DomainError], Vector[Slide]) =
    val results =
      pendingSlides.map { pendingSlide =>
        Slide(
          title = pendingSlide.title,
          elements = pendingSlide.elements,
          layout = pendingSlide.layout,
        )
      }

    val errors =
      results.collect { case Left(errs) => errs }.flatten

    val validSlides =
      results.collect { case Right(slide) => slide }

    (errors, validSlides)
