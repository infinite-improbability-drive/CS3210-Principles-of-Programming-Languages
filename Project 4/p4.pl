% CS3210 Project 4: Prolog
% Last Modified: 12/05/2018
% Created by: Nick Barnes, Heather Minke, Peter Perez, John Samson and
%   John Sanders
% This set of Prolog rules gets the next item from a list of
%   polynomials
%
%   146 394 882 1730 3082
%     248 488 848 1352
%       240 360 504
%         120 144
%           24
%
%   nextItem( [146, 394, 882, 1730, 3082], N).
%   -> N = 5106

% gets the last item in a list
myLast([],[]).
myLast([A],A).
myLast([_|T],A) :- myLast(T,A).

% performs successive subtractions to get the row below
rowBelow([A,B],[C]) :- C is B-A.
rowBelow([A,B|R],[X|Y]) :- X is B-A,
                           rowBelow([B|R],Y).

% creates a list of next items for all rows, exclusing the top row
goDown([A|[]],[A|[]]).
goDown(A,[C|D]) :- myLast(A,C), rowBelow(A,B), goDown(B,D).

% sum of all elements in a list
sumList([],0).
sumList([H|T],R) :- sumList(T, Rest),
                  R is H + Rest.

% gets the next item N from a list of polynomials A
nextItem(A,N) :- goDown(A,B), sumList(B,N).
