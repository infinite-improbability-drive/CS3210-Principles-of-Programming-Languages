#lang racket
(define (firstTour j n)
 (cond
   [(= j n) (cons n '())]
   [(cons j(firstTour(+ j 1)n))]
   )
 )

(define (genTours n)
 (insertAtFront (permutations (firstTour 2 n)))

)

(define (insertAtFront n)
 (cond
  [(= 0 (length n)) '()]
  [#t (cons (cons 1 (car n)) (insertAtFront(cdr n)))]
  )
 )

(define (hypotenuse n m)
  (sqrt
   (+
    (sqr (- (car m) (car n)))
    (sqr (- (cdr m) (cdr n)))
    ))
 )


