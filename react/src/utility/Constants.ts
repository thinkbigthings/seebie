import {ChallengeData, ChallengeList} from "../types/challenge.types";
import {DateRange} from "../types/sleep.types";
import {LocalDate} from "@js-joda/core";

export const HISTOGRAM_COLORS = ['#897b9c', '#596b7c', '#393b4c'];

export const HISTOGRAM_BIN_SIZE_OPTIONS = [
    {value: 60, text: '60 minutes'},
    {value: 30, text: '30 minutes'},
    {value: 15, text: '15 minutes'},
];

export const HISTOGRAM_OPTIONS = {
    plugins: {
        legend: {
            display: false
        },
    },
    scales: {
        x: {
            stacked: false,
            grid: {
                offset: true
            },
            ticks: {
                color: 'white',
                font: {
                    size: 18,
                }
            }
        },
        y: {
            stacked: false
        }
    }
}

export const emptyEditableChallenge = ():ChallengeData => {

    const startLocalDate = LocalDate.now();
    const finishLocalDate = LocalDate.now().plusDays(14);

    const start = new Date();
    const finish = new Date();
    finish.setDate(start.getDate() + 14);

    return {
        id: 0,
        name: "",
        description: "",
        start: startLocalDate,
        finish: finishLocalDate,
    };
}

export const emptyChallengeDataArray: ChallengeData[] = [];

export const emptyChallengeList: ChallengeList<ChallengeData> = {
    current: [],
    upcoming: [],
    completed: []
};

export interface NameDescription {
    name: string,
    description: string
}

export const PREDEFINED_CHALLENGES:NameDescription[] = [
    {
        name: "Consistent bedtime",
        description: "Go to bed at the same time every day even on weekends"
    },
    {
        name: "Proper wind down",
        description: "Do something quiet and relaxing in low light for 1-2 hours before bed instead of looking at your phone or watching a show"
    },
    {
        name: "Reduce Caffeine",
        description: "Cut your caffeine in half, finish all caffeine before lunch, or eliminate caffeine entirely"
    },
    {
        name: "No alcohol before bed",
        description: "Do not consume alcohol within 4 hours of bedtime"
    },
    {
        name: "No Nighttime clock",
        description: "Don't look at the clock during the night"
    },
    {
        name: "Natural sunlight",
        description: "Get 30 minutes of natural sunlight outside as early in the day as you can"
    },
    {
        name: "Meditate daily",
        description: "Meditate at the same time every day, even just 5 minutes."
    },
    {
        name: "Cool overnight temperature",
        description: "Set the overnight temperature in your bedroom to 60-68 degrees Fahrenheit, and/or use a cooling mattress"
    },
    {
        name: "Sleep restriction therapy",
        description: "The amount of time in bed is deliberately limited to match actual sleep time, " +
            "helping to consolidate sleep and increase sleep efficiency. " +
            "This involves a strict sleep schedule, avoiding staying in bed while awake, " +
            "and gradually increasing sleep time as sleep efficiency improves."
    },
    {
        name: "Maintenance",
        description: "Maintain current habits for next three months to make sure your sleep doesn't degrade over time"
    }
];

export const createRange = (lastNDays: number): DateRange => {

    let today = LocalDate.now()
    let previous = today.minusDays(lastNDays);

    return {from: previous, to: today};
}