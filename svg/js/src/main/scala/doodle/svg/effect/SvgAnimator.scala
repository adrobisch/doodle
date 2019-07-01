package doodle
package svg
package effect

import cats.Monoid
import cats.effect.IO
import doodle.effect.Renderer
import doodle.interact.effect.Animator
import monix.eval.{Task, TaskLift}
import monix.execution.Scheduler
import monix.reactive.{Consumer, Observable}

object SvgAnimator extends Animator[Canvas] {
  def animate[Alg[x[_]] <: doodle.algebra.Algebra[x], F[_], A, Frm](canvas: Canvas)(
    frames: Observable[doodle.algebra.Picture[Alg, F, A]])(
    implicit e: Renderer[Alg, F, Frm, Canvas],
    s: Scheduler,
    m: Monoid[A]): IO[A] =
    frames
      .mapEval{img => Task.from(e.render(canvas)(img))}
      .consumeWith(Consumer.foldLeft(m.empty) { (accum, a) =>
        m.combine(accum, a)
      })
      .to[IO](TaskLift.toIO(Task.catsEffect(s)))
}
