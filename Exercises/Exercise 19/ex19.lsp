(define (insert x list)
 (cond
   [(null? list) (cons x list)]
   [(> x (car list)) (cons (car list) (insert x (cdr list)))]
   [#t (cons x list)]
   )
 )