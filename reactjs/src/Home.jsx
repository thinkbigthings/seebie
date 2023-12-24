import React from 'react';
import CollapsibleContent from "./component/CollapsibleContent";
import Container from "react-bootstrap/Container";
import {PREDEFINED_CHALLENGES} from "./utility/Constants";

function Home() {

    return (

        <Container className="overflow-y-scroll h-90vh ">

            <h3>Seebie Sleep Diary FAQ</h3>

            <CollapsibleContent title="What is Seebie?">
                Seebie is a sleep diary app that helps you get better sleep.
            </CollapsibleContent>

            <CollapsibleContent title="How does this improve my sleep?">
                Use a "Challenge" to try a new sleep habit or routine. After the challenge is over,
                If you can see the improvement in your sleep, it will help motivate you to keep that routine!
            </CollapsibleContent>

            <CollapsibleContent title="How do Challenges work?">
                A "Challenge" in this app is a period of time during which you try a new sleep habit or routine.
                Here's how it works:
                <p />
                <ol>
                    <li>Select a Challenge: Open the Challenge page and tap the '+' button. Choose from a list of
                        popular sleep improvement ideas or create your own.
                    </li>
                    <li>Set Your Dates: Choose when to start and end your challenge.</li>
                    <li>Track Your Progress: After completing the challenge, visit the histogram page. Compare your
                        sleep during the challenge with your sleep before it.
                    </li>
                    <li>See the Results: If your sleep improved, consider keeping the new habit and start another
                        challenge to further enhance your sleep quality.
                    </li>
                </ol>
                <p />
                Get started now and see the difference a good sleep habit can make!"
            </CollapsibleContent>

            <CollapsibleContent title="How long does it take to improve sleep?">
                You should spend at least two weeks establishing your baseline sleep
                and at least two weeks per challenge to compare the effectiveness of different sleep habits.
            </CollapsibleContent>

            <CollapsibleContent title="How long should I use this app?">
                Given two-week-long challenges, in as little as one month you may see improvements in your sleep.
                But old habits die hard: it's best to continue to monitor your sleep to maintain your acquired habits
                and keep you on track. Maintaining your sleep habits is just as important as establishing them.
            </CollapsibleContent>

            <CollapsibleContent title="What are some things I can try?">
                <div className={"mb-2"}>
                    When you go to the Challenges section to add a challenge, you will be able to see some common ideas.
                    Here they are listed for your convenience:
                </div>
                <ul>
                    {PREDEFINED_CHALLENGES.map(challenge => <li>{challenge.description}</li>)}
                </ul>
            </CollapsibleContent>

        </Container>
    );

}

export default Home;
