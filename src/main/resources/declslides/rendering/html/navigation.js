document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("presentation-root");
    if (!container) return;

    const slides = Array.from(container.querySelectorAll("[data-slide]"));
    if (slides.length === 0) return;

    const isEditableTarget = (target) => {
        if (!(target instanceof HTMLElement)) return false;
        const tagName = target.tagName;
        return target.isContentEditable ||
            tagName === "INPUT" ||
            tagName === "TEXTAREA" ||
            tagName === "SELECT";
    };

    const currentIndex = () => {
        const top = container.scrollTop;
        let nearestIndex = 0;
        let nearestDistance = Number.POSITIVE_INFINITY;

        slides.forEach((slide, index) => {
            const distance = Math.abs(slide.offsetTop - top);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestIndex = index;
            }
        });

        return nearestIndex;
    };

    const goTo = (index) => {
        if (index < 0 || index >= slides.length) return;
        slides[index].scrollIntoView({
            behavior: "smooth",
            block: "start",
        });
    };

    window.addEventListener("keydown", (event) => {
        if (isEditableTarget(event.target)) return;

        if (event.key === "ArrowRight") {
            event.preventDefault();
            goTo(currentIndex() + 1);
        }

        if (event.key === "ArrowLeft") {
            event.preventDefault();
            goTo(currentIndex() - 1);
        }
    });
});