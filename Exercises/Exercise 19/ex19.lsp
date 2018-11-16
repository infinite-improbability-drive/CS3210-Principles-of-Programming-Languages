(define (insert x list)
 (cond
   [(null? list) (cons x list)]
   [(> x (car list)) (cons (car list) (insert x (cdr list)))]
   [#t (cons x list)]
   )
 )
 
 (define (sort list)
 (cond
   [(null? list) list]
   [#t (insert (car list) (sort (cdr list)))]
 )
)