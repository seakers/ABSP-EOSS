
function [] = gndPlot(varargin)
    clc; close all;
       
    % Read JSON input file
    [fileName,fileDir,~] = uigetfile('../inputs/*.json*',...
        'Select Input File');
    filePath = [fileDir fileName];
    
    jsonData = jsondecode( fileread(filePath) );
    
    simName = jsonData.simulation.name;
    resDir = uigetdir('../results/','Select Results Directory');
    
    % Load Ortbit Data
    sensingSatsData = readcell( '../data/constellations/' + string(jsonData.simulation.constellation) + '.xls', 'Sheet', 'Remote Sensing');
    commsSatsData = readcell( '../data/constellations/' + string(jsonData.simulation.constellation) + '.xls', 'Sheet', 'Communications');
    gndStatData = readcell( '../data/databases/GroundStationDatabase.xls', 'Sheet', jsonData.simulation.groundStationNetwork);
    [n_sense ~] = size(sensingSatsData);
    [n_comms ~] = size(commsSatsData);
    [n_gnd ~] = size(gndStatData);
    
    satNames = [];
    for i = 2:n_sense
       satNames = [satNames; string(sensingSatsData{i,2})];
    end
    for i = 2:n_comms
       satNames = [satNames; string(commsSatsData{i,2})]; 
    end
    
    sats = cell( length(satNames),1 );
    j = 1;
    for i = 2:n_sense
        name = sensingSatsData{i,2};
        alt = sensingSatsData{i,3};
        fov = 50;
        sats{j} = Sat(name, alt, fov, jsonData);
        j = j + 1;
    end
    
    for i = 2:n_comms
        name = commsSatsData{i,2};
        alt = commsSatsData{i,3};
        fov = commsSatsData{i,8};
        sats{j} = Sat(name, alt, fov, jsonData);
        j = j + 1;
    end
    
    gndStatNames = [];
    for i = 2:n_gnd
        gndStatNames = [gndStatNames; string(gndStatData{i,1})];
    end
    
    % Load Ground Points and Stations
    gndPts = CovDef(jsonData);
    gndStats = GndStats(jsonData);
    
    % Load Requests and Measurement 
    requests = measReqs(resDir);
    measurements = readcell(resDir + "/run_0/measurements.csv", 'Delimiter',',');
    
    % Load Messages 
    messages = readcell(resDir + "/run_0/messages.csv", 'Delimiter',',');
    
    
    % Generate Plot
    
    worldmap world
    load coastlines
    
    [latcells, loncells] = polysplit(coastlat, coastlon);
    plotm(coastlat, coastlon, 'black')
    hold on
    x0=200;
    y0=200;
    width=1000;
    height=1000;
    set(gcf,'position',[x0,y0,width,height])

    t0 = tic;
    t = sats{1}.T;
    n_draw = 10;
    
    lines = cell(size(sats));
    points = cell(size(sats));
    circles = cell(size(sats));
    for j = 1:length(sats)
        lines{j} = plotm(0,0);
        points{j} = plotm(0,0);
        circles{j} = circlem(0,0,0);
    end
    
    plotm([gndPts.DefData{:,3}], [gndPts.DefData{:,4}], '.', 'Color', [1, 1, 1]*.75,'MarkerSize',5);
    reqPoints = plotm(requests.Lat, requests.Lon, '*', 'Color', [1, 1, 1]*.75,'MarkerSize', 7.5);
    plotm([gndStats.DefData{:,3}], [gndStats.DefData{:,4}], 'vk','MarkerSize',7.5);
    
    di = 60;
    dt = t(di) - t(1);
    n_made = 0;
    for i = 1:di:length(t)  
        
        % Draw sat position
        if i <= n_draw
           for j = 1:length(sats)
               sat = sats{j};
               lines{j} = plotm(sat.Lat(1:i),sat.Lon(1:i));
           end
        else
           for j = 1:length(sats)
               sat = sats{j};
               lines{j} = plotm(sat.Lat(i-n_draw:i),sat.Lon(i-n_draw:i));
           end
        end
        
        for j = 1:length(sats)
           sat = sats{j};
           points{j} = plotm(sat.Lat(i),sat.Lon(i),'s', 'MarkerSize', 10);
           circles{j} = circlem(sat.Lat(i),sat.Lon(i),...
               sat.R_fov, 'units', validateLengthUnit('kilometer'),...
               'facecolor', 'b',...
               'FaceAlpha', 0.5,...
               'EdgeAlpha', 0.5);
        end
        
        % Draw active asks
        activeTasks = [];
        for j = 1:length(requests.ReqData(:,1))
            if t(i) >= requests.ReqData{j,6} ...
                    && t(i) <= requests.ReqData{j,7}
                activeTasks = [activeTasks; 
                               requests.Lat(j), requests.Lon(j)]; 
            end
        end
        if length(activeTasks) > 0
            reqPoints = plotm(activeTasks(:,1), activeTasks(:,2), '*r',...
                                'MarkerSize', 7.5);
        end
        
        % Draw Messages
        messagesSent = [];
        for j = 1:length(messages(:,1))
            if t(i)-dt <= messages{j,5} ...
                    && t(i) >= messages{j,5}
                if length(messagesSent) > 0 ...
                    && ~isempty( find(messagesSent(:,1) == string(messages{j,1})) )...
                    && ~isempty( find(messagesSent(:,2) == string(messages{j,2})) )...
                    && find(messagesSent(:,1) == string(messages{j,1})) == find(messagesSent(:,2) == string(messages{j,2}))
                        continue;
                end
                messagesSent = [messagesSent; 
                               string(messages{j,1}), string(messages{j,2}), string(messages{j,3})]; 
            end
        end
        
        [n,~] = (size(messagesSent));
        messageLines = cell(n,1);
        
        for k = 1:n
            
            % find position of sender and reciever of message at time t
            if isempty( find(gndStatNames == messagesSent(k,1)) )
                i_sender = find(satNames == messagesSent(k,1));
                sat = sats{i_sender};
                
                sender = [sat.Lat(i),sat.Lon(i)];
            else
                i_sender = find(gndStatNames == messagesSent(k,1));
                sender = [gndStatData{i_sender+1,3},gndStatData{i_sender+1,4}];
            end
            
            if isempty( find(gndStatNames == messagesSent(k,2)) )
                i_reciever = find(satNames == messagesSent(k,2));
                sat = sats{i_reciever};
                
                reciever = [sat.Lat(i),sat.Lon(i)];
                
            else
                i_reciever = find(gndStatNames == messagesSent(k,2));
                reciever = [gndStatData{i_reciever+1,3},gndStatData{i_reciever+1,4}];
            end
            
            if messagesSent(k,3) == "Measurement"
                messageLines{k} = linem([sender(1); reciever(1)],[sender(2); reciever(2)],'--g');
            elseif messagesSent(k,3) == "Request"
                messageLines{k} = linem([sender(1); reciever(1)],[sender(2); reciever(2)],'--r');
            end
        end
        
        % Draw Measurements
        measurementsMade = [];
        for j = 1:length(measurements(:,1))
            if t(i)-dt <= measurements{j,12} ...
                    && t(i) >= measurements{j,12}
                measurementsMade = [measurementsMade; 
                               string(measurements{j,1}), string(measurements{j,3}), ...
                               string(measurements{j,4}),string(measurements{j,12})]; 
            end
        end
        
        if length(measurementsMade) > 0
            x = 1;
        end 
        
        [n,~] = (size(measurementsMade));
        measurementLines = cell(n,1);
        
        for k = 1:n
            i_sat = find(satNames == measurementsMade(k,1));
            sat = sats{i_sat};

            sender = [sat.Lat(i),sat.Lon(i)];
            reciever = [NaN, NaN];
                            
            [n,~] = size(gndPts.DefData);
            covDef = convertStringsToChars(measurementsMade(k,2));
            pnt = str2double(measurementsMade(k,3));
            
            for z = 1:n
                if prod(gndPts.DefData{z,1} == covDef) && gndPts.DefData{z,2} == pnt
                   reciever(1) = gndPts.DefData{z,3};
                   reciever(2) = gndPts.DefData{z,4};
                   break;
                end
            end
            
             measurementLines{k} = linem([sender(1); reciever(1)],[sender(2); reciever(2)],'*g');
             n_made = n_made + 1;
        end
        
        
        % Draw and wait
        drawnow;
        while toc(t0) < 1/3
            %wait     
        end
        
        % Delete and prep for next iteration
        tcurr = t(i)/60;
        title(["Current Time: "+tcurr+" [mins]";
               "Number of ActiveTasks: "+length(activeTasks);
               "Number of Measurements Made: " + n_made]);
        
        for j = 1:length(messageLines)
           delete(messageLines{j}) 
        end
        
        for j = 1:length(measurementLines)
            delete(measurementLines{j})
        end
        
        for j = 1:length(sats)
            delete(lines{j})
            delete(points{j})
            delete(circles{j})
        end        
        
        delete(reqPoints);
        reqPoints = plotm(requests.Lat, requests.Lon, '*', 'Color', [1, 1, 1]*.75,'MarkerSize', 7.5);

        t0 = tic;
    end
    
    for j = 1:length(sats)
       sat = sats{j};
       lines{j} = plotm(sat.Lat(:),sat.Lon(:));
%        circles{j} = circlem(sat.Lat(:),sat.Lon(:),...
%                sat.R_fov, 'units', validateLengthUnit('kilometer'),...
%                'facecolor', 'b',...
%                'FaceAlpha', 0.5,...
%                'EdgeAlpha', 0.5);
    end

    disp('DONE')
end