package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should create a new song and return its ID"

    request {
        method POST()
        url "/songs"
        headers {
            contentType(applicationJson())
        }
        body(
                resourceId: 123,
                name: "Song Name",
                artist: "Artist",
                album: "Album",
                length: "3:45",
                year: "2023"
        )
    }

    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1
        )
    }
}