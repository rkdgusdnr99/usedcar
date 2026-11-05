package hello.usedcar.web;

import hello.usedcar.domain.Car;
import hello.usedcar.domain.CarStatus;
import hello.usedcar.dto.CarSaveRequest;
import hello.usedcar.dto.CarSearchCond;
import hello.usedcar.dto.CarUpdateRequest;
import hello.usedcar.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    // 이 컨트롤러의 모든 화면에서 ${statuses}로 쓸 수 있다
    @ModelAttribute("statuses")
    public CarStatus[] statuses() {
        return CarStatus.values();
    }

    @GetMapping
    public String list(@ModelAttribute("cond") CarSearchCond cond, Model model) {
        List<Car> cars = carService.findCars(cond);
        model.addAttribute("cars", cars);
        return "cars/list";
    }

    @GetMapping("/{carId}")
    public String detail(@PathVariable Long carId, Model model) {
        Car car = carService.findCar(carId); // 없으면 CarNotFoundException → 404 오류 페이지
        model.addAttribute("car", car);
        return "cars/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("car", new CarSaveRequest());
        return "cars/addForm";
    }

    @PostMapping("/add")
    public String add(@Validated @ModelAttribute("car") CarSaveRequest request,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes) {

        // 애노테이션으로 표현하기 어려운 검증은 직접 한다
        if (request.getCarNumber() != null && carService.isDuplicateCarNumber(request.getCarNumber())) {
            bindingResult.rejectValue("carNumber", "duplicate");
        }

        if (bindingResult.hasErrors()) {
            return "cars/addForm"; // 입력값과 오류를 그대로 들고 폼으로 돌아간다
        }

        Car saved = carService.register(request);
        redirectAttributes.addAttribute("carId", saved.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/cars/{carId}"; // PRG: 등록 후 상세 화면으로 리다이렉트
    }

    @GetMapping("/{carId}/edit")
    public String editForm(@PathVariable Long carId, Model model) {
        Car car = carService.findCar(carId);
        model.addAttribute("carInfo", car);
        model.addAttribute("car", new CarUpdateRequest(car.getMileage(), car.getPrice()));
        return "cars/editForm";
    }

    @PostMapping("/{carId}/edit")
    public String edit(@PathVariable Long carId,
                       @Validated @ModelAttribute("car") CarUpdateRequest request,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("carInfo", carService.findCar(carId));
            return "cars/editForm";
        }

        carService.update(carId, request);
        return "redirect:/cars/{carId}";
    }
}