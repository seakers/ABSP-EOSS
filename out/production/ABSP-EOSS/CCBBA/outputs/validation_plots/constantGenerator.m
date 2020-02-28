clc;
v1 = 1;
v2 = 0.5;
x0 = -1;
x = [0 1 2 3 4 5 6 7 8 9];
t_corr = [0 1 2 3 4 5 6 7 8 9];

t_2f = (x - x0)./v2;
t_1f = zeros(1,10);
lambda = zeros(1,10);
for i = 1:length(t_1f)
    if(i == 1)
        t_1f(i) = (x(i) - x0)/v1;
    else
        t_temp = t_2f(i-1) - t_corr(i) + (x(i) - x(i-1))/v1;
        t_quickest = (x(i) - x0)/v1;
        if t_temp > t_quickest
           t_1f(i) = t_temp;
        else
            t_1f(i) = t_quickest;
        end
    end
    
    syms L;
    lambda(i) = vpasolve( exp(L*(t_1f(i)))*( exp(-L*(t_2f(i))) + exp(-L*(t_2f(i) - t_corr(i))) ) - 2/3 == 0 , L);
end

fprintf("\t\t x\t  t_corr\t  t_1f\t\tt_2f\tlambda\n");
disp([x', t_corr', t_1f', t_2f', lambda'])
