; CS3210 Project 3: Lisp
; Last Modified 12/04/2018
; Created by: Nick Barnes, Heather Minke, Peter Perez, John Samson and John Sanders
; Solution to Euclidian Traveling Salesman Problem


#lang racket

; creates a list numbers from j to n
(define (firstTour j n)
  (cond
    [(= j n) (cons n '())]
    [(cons j(firstTour(+ j 1)n))]
    )
  )

; generates possible tours of length n
(define (genTours n)
  (insertAtFront (permutations (firstTour 2 n)))
  )

; generates pairs of scores and tours for purposes of sorting and indexing
(define (genPairs a)
  (sort(addPair(findBest a (genTours (length a))) (genTours (length a))))
  )

; generates a single optimatal tour from a list of points
(define (etsp a)
  (car (cdr (car (sort(addPair(findBest a (genTours (length a))) (genTours (length a)))))))
  )

; insert an element into the front of list n
(define (insertAtFront n)
  (cond
    [(= 0 (length n)) '()]
    [#t (cons (cons 1 (car n)) (insertAtFront(cdr n)))]
    )
  )

; insert an element into list at index x
(define (insert x list)
  (cond
    [(null? list) (cons x list)]
    [(< (car x) (car (car list))) (cons x list)]
    [#t (cons(car list) (insert x (cdr list)))]
    )
  )

; sort a list of ordered pairs by first element
(define (sort list)
  (cond
    [(null? list) list]
    [#t (insert (car list) (sort (cdr list)))]
    )
  )

; create list of ordered pairs from two lists
(define (addPair list1 list2)
  (cond
    [(null? list1) '()]
    [(null? list2) '()]   
    [#t (cons(cons (car list1) (cons (car list2) '())) (addPair (cdr list1) (cdr list2)))]
    )
  )

; generate scores from a list of points and tours
(define (findBest points tours)
  (cond
    [(= (length tours) 0) '()]
    [#t (cons (score points (car tours))(findBest points (cdr tours)))]
    )
  )

; get distance between two points
(define (hypo n m)
  (cond
    [(null? m) 0]
    [#t (sqrt(+(sqr(- (car n) (car m))) (sqr(- (car(cdr n)) (car(cdr m))))))]
    )
  )

; calculate score given list of points x and tour y
(define (score x y)
  (cond
    [(= (length y) 1)(hypo (car x )(getItem (- (length x) 1) x))]
    [(null? (cdr x)) 0]
    [#t (+(hypo (getItem (-(car y) 1)  x) (getItem (-(car (cdr y)) 1) x))
         (score x (cdr y)))]
    )
  )

; get list element at index n
(define (getItem index n)
  (cond
    [(null? n) '()]
    [(> index 0) (getItem (- index 1) (cdr n))]
    [(= index 0) (car n)]
    )
  )

; removes an element at index from list n 
(define (getRest index n)
  (if (> index 0)
    (cons (car n) (getRest (- index 1) (cdr n))) (cdr n)
    )
  )