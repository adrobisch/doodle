package doodle

import doodle.effect.Renderer
import doodle.interact.algebra.MouseOver
import doodle.interact.effect.Animator
import doodle.language.Basic
// import monix.reactive.Observer
import org.scalajs.dom

package object svg {
  import doodle.interact.algebra.Redraw
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F] with MouseOver[F]
  type Tag = scalatags.generic.TypedTag[dom.Element, dom.Element, dom.Node]
  type SvgResult[A] = (Tag, A)
  type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]

  type Frame = doodle.svg.effect.Frame
  type Canvas = doodle.svg.effect.Canvas
  implicit val svgRenderer: Renderer[Algebra, Drawing, Frame, Canvas] =
    doodle.svg.effect.SvgRenderer
  implicit val svgAnimator: Animator[Canvas] =
    doodle.svg.effect.SvgAnimator
  implicit val svgRedraw: Redraw[Canvas] =
    doodle.svg.algebra.Redraw

  implicit val svgScheduler = monix.execution.Scheduler.global

  val Frame = doodle.svg.effect.Frame

  type Picture[A] = doodle.algebra.Picture[Algebra, Drawing, A]
  object Picture {
    def apply(f: Algebra[Drawing] => Drawing[Unit]): Picture[Unit] = {
      new Picture[Unit] {
        def apply(implicit algebra: Algebra[Drawing]): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
