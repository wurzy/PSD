-module(testes).
-export(run/0).

run()->
    Q1 = myqueue:create(),
    Q2 = myqueue:enqueue(Q1,1),
    Q3 = myqueue:enqueue(Q2,2),
    Q4 = myqueue:enqueue(Q3,3),
    Q5 = myqueue:enqueue(Q4,4),
    Q6 = myqueue:enqueue(Q5,5),
    Q8 = myqueue:dequeue(Q6),
    Q9 = myqueue:dequeue(Q8),
    Q10 = myqueue:dequeue(Q9),
    Q11 = myqueue:dequeue(Q10),
    Q12 = myqueue:dequeue(Q11),
    ok.