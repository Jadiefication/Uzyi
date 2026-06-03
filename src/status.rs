#[derive(PartialEq)]
pub enum Status {
    Running,
    // When to wake up
    Sleeping(u64),
    Stopped
}