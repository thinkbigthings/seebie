import React from 'react';
import CollapsibleContent from "./component/CollapsibleContent";
import Container from "react-bootstrap/Container";
import {PREDEFINED_CHALLENGES} from "./utility/Constants";

function Home() {

    return (

        <Container className="overflow-y-scroll h-90vh ">

            <h3>Seebie Sleep Diary FAQ</h3>

            <CollapsibleContent title="What is Seebie?">
                Seebie is a sleep diary app that actively helps you get better sleep, no device necessary.
            </CollapsibleContent>

            <CollapsibleContent title="Why should I track my sleep?">
                Keeping track of your sleep yourself can increase your self awareness and encourage healthy sleep behavior.
            </CollapsibleContent>

            <CollapsibleContent title="How does this improve my sleep?">
                You can track your sleep to establish a baseline, challenge yourself to try a new evening routine, then
                compare the two time periods to see how the new routine affected your sleep.
                If you can see the improvement, it will help motivate you to keep that routine!
            </CollapsibleContent>

            <CollapsibleContent title="How long does it take to improve sleep?">
                You should spend at least two weeks establishing your baseline sleep
                and at least two weeks per experiment to compare the effectiveness of different sleep habits.
            </CollapsibleContent>

            <CollapsibleContent title="How long should I use this app?">
                In as little as one month you may see improvements in your sleep.
                But old habits die hard: it's best to continue to monitor your sleep to maintain your acquired habits
                and keep you on track. Maintaining your sleep habits is just as important as establishing them.
            </CollapsibleContent>

            <CollapsibleContent title="What are some things I can try?">
                <div className={"mb-2"}>When you go to the Challenges section to add a challenge, you will be able to see some common ideas.
                    Here they are listed for your convenience:</div>
                <ul>
                    {PREDEFINED_CHALLENGES.map(challenge => <li>{challenge.description}</li>)}
                </ul>
            </CollapsibleContent>

        </Container>
    );

}

export default Home;
