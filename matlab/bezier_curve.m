% Bezier curve
% Author: Henrik Karlsson
% 
% Returns the coordinates (n x 2) of a 2nd degree bezier curve.
%
% A: start point
% B: mid control point
% C: end point
% h: approximate distance between each coordinate.

function ans = bezier_curve(A, B, C, h)
dt = @(t) 1 / norm( 2*(A-2*B+C)*t + (-2*A+2*B) );
t = 0;
tt = [];
while t < 1
    tt = [tt ; t];
    
    % Runge-Kutta 4 
    k1 = dt(t);
    k2 = dt(t + h/2*k1);
    k3 = dt(t + h/2*k2);
    k4 = dt(t + h*k3);
    
    t = t + (k1 + 2 * k2 + 2 * k3 + k4) * h / 6;
end
ans = ((1 - tt).^2) * A + 2 * (tt .* (1 - tt)) * B + tt.^2 * C;
end