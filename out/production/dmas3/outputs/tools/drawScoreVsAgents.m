function [] = drawScoreVsAgents(n)
    data_all = {};
    
    data_1 = readResults("ScoreVsAgents_1",n);
    data_2 = readResults("ScoreVsAgents_2",n);
    data_3 = readResults("ScoreVsAgents_3",n);
    
    data_all{1} = data_1;
    data_all{2} = data_2;
    data_all{3} = data_3;
    
    scores = zeros(n,3);
    for i = 1:3
       scores(:,i) = data_all{i}(:,4)./data_all{i}(:,5)*100;
    end
    
    t1 = repmat({'1 sensor type'; '3 non-constrained agents'},n,1);
    t2 = repmat({'2 sensor types'; '6 constrained agents'},n,1);
    t3 = repmat({'3 sensor types'; '9 constrained agents'},n,1);
    x = [t1; t2; t3];
    
    figure
    boxplot(scores,x);
    grid on;
    ylabel({'Score Achieved/' ; 'Score Available [%]'});
    title('Non-dimensional score achieved by all agents');
end