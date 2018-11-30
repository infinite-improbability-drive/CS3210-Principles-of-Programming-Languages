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

(define (score x y)
  (cond
    [(= (length y) 1)(hypo (car x )(getItem (- (length x) 1) x))]
    [(null? (cdr x)) 0]
    [#t (+(hypo (getItem (-(car y) 1)  x) (getItem (-(car (cdr y)) 1) x))
         (score x (cdr y)))]
  )
)
(define (hypo n m)
  (cond
    [(null? m) 0]
    [#t(sqrt(+(sqr(- (car n) (car m))) (sqr(- (car(cdr n)) (car(cdr m))))))]
  )
)

(define (getItem index n)
  (cond
    [(null? n) '()]
    [(> index 0) (getItem (- index 1) (cdr n))]
    [(= index 0) (car n)]
    )
)

(define (getRest index n)
  (if (> index 0)
      (cons (car n) (getRest (- index 1) (cdr n)))
      (cdr n)))