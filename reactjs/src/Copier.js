

// https://stackoverflow.com/questions/122102/what-is-the-most-efficient-way-to-deep-clone-an-object-in-javascript/10916838

// Can also use Lodash to copy, it has a ton of other utilities too

function copy(obj) {
    return JSON.parse(JSON.stringify(obj)); // deep copy but not methods, and not some other data structures
}

export default copy;
