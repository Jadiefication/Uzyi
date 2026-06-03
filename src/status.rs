#[derive(PartialEq)]
pub enum Status {
    Running,
    // When to wake up
    Sleeping(u64),
    Stopped
}

impl Status {

    pub fn collapse(&self) -> u8 {
        match self {
            Status::Running => 0,
            Status::Sleeping(_) => 1,
            Status::Stopped => 2
        }
    }
}