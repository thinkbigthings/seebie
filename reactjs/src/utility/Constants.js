
const VERSION_HEADER = 'X-Version';

// picks up from .env file in build
const { VITE_API_VERSION } = import.meta.env;

export {VERSION_HEADER, VITE_API_VERSION};

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