import React, {useEffect, useState} from 'react';

import {
    CategoryScale,
    Chart as ChartJS, type ChartOptions,
    Filler,
    Legend,
    LinearScale,
    LineElement,
    PointElement,
    Title,
    Tooltip
} from "chart.js";
import {Line} from 'react-chartjs-2';
import Container from "react-bootstrap/Container";
import {GET} from "./utility/BasicHeaders";
import {NavHeader} from "./App";
import DateRangePicker from "./component/DateRangePicker";
import {useParams} from "react-router-dom";
import {createRange} from "./utility/Constants";
import {DateRange} from "./types/sleep.types";
import {localDateToJsDate, jsDateToLocalDate, localDateToString} from "./utility/Mapper.ts";


ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Filler,
    Legend
);

const chartOptions: ChartOptions<'line'> = {
    scales: {
        y: {
            beginAtZero: true,
            title: {
                text: 'Hours Asleep',
                display: true
            }
        }

    },
    plugins: {
        legend: {
            display: false,
            position: 'top'
        },
        // title: {
        //     display: true,
        //     text: 'Hours Asleep Chart',
        // },
    },
};

const initialChartData = {
    datasets: [{
        fill: true,
        data: [],
        borderColor: '#745085',
        backgroundColor:'#595b7c'
    }]
};

function SleepChart(props:{createdCount:number}) {

    const {publicId} = useParams();

    const {createdCount} = props;

    let [range, setRange] = useState(createRange(30));
    let [chartData, setChartData] = useState(initialChartData);

    function updateSearchRange(updateValues:Partial<DateRange>) {
        let updatedRange = {...range, ...updateValues};
        if( updatedRange.from.isBefore(updatedRange.to) ) {
            setRange(updatedRange);
        }
    }

    let requestParameters = '?'
        + 'from='+encodeURIComponent(localDateToString(range.from)) + '&'
        + 'to='+encodeURIComponent(localDateToString(range.to));

    const sleepEndpoint = `/api/user/${publicId}/sleep/chart${requestParameters}`;

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(json => {
                let newChartData = structuredClone(initialChartData);
                newChartData.datasets[0].data = json;
                setChartData(newChartData);
            })
    }, [sleepEndpoint, createdCount]);

    const chartArea = chartData.datasets[0].data.length > 1
        ?   <Line className="pt-3" datasetIdKey="sleepChart" options={chartOptions} data={chartData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    return (
        <Container>
            <NavHeader title="Sleep Chart" />

            <DateRangePicker selectedStart={localDateToJsDate(range.from)}
                             onChangeStart={date => updateSearchRange({from: jsDateToLocalDate(date)})}
                             selectedEnd={localDateToJsDate(range.to)}
                             onChangeEnd={date => updateSearchRange({to: jsDateToLocalDate(date)})} />

            {chartArea}

        </Container>
    );
}

export {SleepChart};
