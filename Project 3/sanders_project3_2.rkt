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

(define (insert x list)
  (cond
  [(null? list) (cons x list)]
  [(< (car x) (car (car list))) (cons x list)]
  [#t (cons(car list) (insert x (cdr list)))]
  )
 )
(define (sort list)
 (cond
   [(null? list) list]
   [#t (insert (car list) (sort (cdr list)))]
 )
)

(define (addPair list1 list2)
  (cond
   [(null? list1) '()]
   [(null? list2) '()]   
   [#t (cons(cons (car list1) (cons (car list2) '())) (addPair (cdr list1) (cdr list2)))]
   )
  
 )
(define (findBest points tours)
  (cond
    [(= (length tours) 0) '()];(score points (car tours))]
    [#t (cons (score points (car tours))(findBest points (cdr tours)))]
    )

  )


(define (etsp a)
  (cdr(car(sort(addPair(findBest a (genTours (length a))) (genTours (length a))))))
 )
  
(define (score x y)
  (cond
    [(= (length y) 1)(hypo (car x) (getItem (-(car y) 1) x))]
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