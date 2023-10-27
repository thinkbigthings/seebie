import React from 'react';
import CollapsibleFaq from "./component/CollapsibleFaq";
import Container from "react-bootstrap/Container";

function Home() {

    return (

        <Container className="overflow-y-scroll h-90vh ">

            <h3>Seebie Sleep Diary FAQ</h3>

            <CollapsibleFaq title="What is Seebie?">
                Seebie is a sleep diary app that actively helps you get better sleep, no device necessary.
            </CollapsibleFaq>

            <CollapsibleFaq title="Why should I track my sleep?">
                Keeping track of your sleep yourself can increase your self awareness and encourage healthy sleep behavior.
            </CollapsibleFaq>

            <CollapsibleFaq title="How does this improve my sleep?">
                You can track your sleep baseline, try a new evening routine, then
                compare the two time periods to see how the new routine affected your sleep.
                If you can see the improvement, it will help motivate you to keep that routine!
            </CollapsibleFaq>

            <CollapsibleFaq title="How long does it take to improve sleep?">
                You should spend at least two weeks establishing your baseline sleep
                and at least two weeks per experiment to compare the effectiveness of different sleep habits.
            </CollapsibleFaq>

            <CollapsibleFaq title="How long should I use this app?">
                In as little as one month you may see improvements in your sleep.
                But old habits die hard: it's best to continue to monitor your sleep to maintain your acquired habits
                and keep you on track. Maintaining your sleep habits is just as important as establishing them.
            </CollapsibleFaq>

            <CollapsibleFaq title="What are some routines I can try?">
                <ul>
                    <li>Go to bed at the same time every day, even on weekends</li>
                    <li>Do something quiet and relaxing in low light for 60 minutes before bed instead of looking at your phone or watching a show</li>
                    <li>Cut your caffeine in half, or finish all caffeine for the day before lunch</li>
                    <li>Don't look at the clock during the night</li>
                </ul>
            </CollapsibleFaq>

        </Container>
    );

}

export default Home;
