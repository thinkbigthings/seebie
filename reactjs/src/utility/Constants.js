
const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { VITE_API_VERSION } = import.meta.env;

export {VERSION_HEADER, VITE_API_VERSION};

export const PREDEFINED_CHALLENGES = [
    {
        title: "Consistent bedtime",
        description: "Go to bed at the same time every day even on weekends"
    },
    {
        title: "Proper wind down",
        description: "Do something quiet and relaxing in low light for 1-2 hours before bed instead of looking at your phone or watching a show"
    },
    {
        title: "Reduce Caffeine",
        description: "Cut your caffeine in half, finish all caffeine before lunch, or eliminate caffeine entirely"
    },
    {
        title: "No Nighttime clock",
        description: "Don't look at the clock during the night"
    },
    {
        title: "Natural sunlight",
        description: "Get 30 minutes of natural sunlight outside as early in the day as you can"
    },
    {
        title: "Meditate daily",
        description: "Meditate at the same time every day, even just 5 minutes."
    },
    {
        title: "Cool overnight temperature",
        description: "Set the overnight temperature in your bedroom to 60-68 degrees Fahrenheit, and/or use a cooling mattress"
    },
    {
        title: "Maintenance",
        description: "Maintain current habits for next three months to make sure your sleep doesn't degrade over time"
    }
];