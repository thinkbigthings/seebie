// This custom hook is used to fetch challenge data from an API endpoint and
// transform it into a format suitable for the UI. By encapsulating all query
// logic in this hook, we ensure that every component that consumes this data gets the same output.
// It also prevents duplication of code and minimizes the risk of divergence if
// the query configuration ever needs to change.

import { useQuery } from '@tanstack/react-query';
import {ChallengeData, ChallengeDetailDto, ChallengeList} from "../types/challenge.types.ts";
import {toChallengeList} from "../utility/Mapper.ts";
import {GET} from "../utility/BasicHeaders.ts";


// Custom hook that encapsulates the TanStack Query logic for fetching challenges.
// The queryKey uses the challengeUrl to ensure the query is uniquely identified.
export const useChallenges = (challengeUrl: string) => {

    // TODO should this be httpGet()?
    // and should we use httpGet in Tools?

    const fetchChallenges = () => fetch(challengeUrl, GET)
        .then((response) => response.json() as Promise<ChallengeDetailDto[]>);

    return useQuery<ChallengeDetailDto[], Error, ChallengeList<ChallengeData>>({
        queryKey: [challengeUrl],
        queryFn: fetchChallenges,
        placeholderData: [] as ChallengeDetailDto[],
        // Keep the data fresh as long as the component is mounted.
        staleTime: Infinity,
        // The select function transforms the fetched data into UI-friendly objects.
        select: (data: ChallengeDetailDto[]) => toChallengeList(data),
    });
};
