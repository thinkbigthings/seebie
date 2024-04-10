// @ts-nocheck

const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { VITE_API_VERSION } = import.meta.env;

export {VERSION_HEADER, VITE_API_VERSION};

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

export const emptyEditableChallenge = () => {
    const suggestedEndDate = new Date();
    suggestedEndDate.setDate(suggestedEndDate.getDate() + 14);
    return {
        name: "",
        description: "",
        localStartTime: new Date(),
        localEndTime: suggestedEndDate
    };
}

export const emptyChallengeList = {
    current: [],
    upcoming: [],
    completed: []
};

export const PREDEFINED_CHALLENGES = [
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

export interface User {
    username: string;
    roles: string[];
    registrationTime: string;
    personalInfo: {
        displayName: string;
        email: string;
        notificationsEnabled: boolean;
    };
    isLoggedIn: boolean;
}

export const blankUser: User = {
    username: '',
    roles: [],
    registrationTime: '',
    personalInfo: {
        displayName: '',
        email: '',
        notificationsEnabled: false
    },
    isLoggedIn: false,
}

export const createRange = (lastNDays) => {

    let today = new Date();
    today.setHours(23, 59, 59);

    let lastMonth = new Date(today.getTime());
    lastMonth.setDate(today.getDate() - lastNDays);
    lastMonth.setHours(0, 0, 0);

    return {from: lastMonth, to: today};
}